package com.googlecode.iqapps.testtools;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


import android.app.Instrumentation;
import android.util.Log;

public class PauseButton implements Runnable {
	private final static String TAG = Positron.TAG + ".PauseButton";

	private final Instrumentation instrumentation;
	private final Thread thread;
	private final CyclicBarrier threadStarted;
	private final Barrier barrier;
	private final CyclicBarrier entering;
	private final CyclicBarrier left;
	private boolean paused;
	private boolean quit;

	public PauseButton(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
		barrier = new Barrier();
		entering = new CyclicBarrier(2);
		left = new CyclicBarrier(2);
		paused = false;
		quit = false;

		threadStarted = new CyclicBarrier(2);

		thread = new Thread(this);
		thread.start();

		try {
			threadStarted.await();
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted during construction.", e);
		} catch (BrokenBarrierException e) {
			Log.e(TAG, "Interrupted during construction.", e);
		}
	}

	public synchronized void pause() {
		if (paused) return;
		paused = true;
		await(entering);
		await(barrier.entered);
	}

	public synchronized void resume() {
		if (!paused) return;
		await(barrier.leaving);
		await(left);
		paused = false;
	}

	public void quit() {
		Log.v(TAG, "Exiting...");
		barrier.quit = true;
		quit = true;

		entering.reset();
		barrier.entered.reset();
		barrier.leaving.reset();
		left.reset();

		try {
			thread.join();
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted during quit.", e);
		}
	}

	public synchronized boolean paused() { return paused; }

	public void run() {
		Log.v(TAG, "Starting up.");
		try {
			threadStarted.await();
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted during construction.", e);
		} catch (BrokenBarrierException e) {
			Log.e(TAG, "Interrupted during construction.", e);
		}

		while (!quit) {
			await(entering);
			if (quit) break;
			instrumentation.runOnMainSync(barrier);
			await(left);
		}
		Log.v(TAG, "Exited.");
	}

	private static class Barrier implements Runnable {
		public final CyclicBarrier entered;
		public final CyclicBarrier leaving;
		public boolean quit = false;

		public Barrier() {
			entered = new CyclicBarrier(2);
			leaving = new CyclicBarrier(2);
		}

		public void run() {
			try {
				if (!quit) entered.await();
				if (!quit) leaving.await();
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (BrokenBarrierException e) {}
		}
	}

	private void await(CyclicBarrier barrier) {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (BrokenBarrierException e) {}
	}
}
