# Introduction #

This application is the aggregation of three others on the Palm and Android platforms.  I use this application daily for tracking time worked against various projects.  My requirements call for recording my time in 6-minute increments, but it accommodates a number of increments.

# Details #

Time input is organized by tasks or projects presented on the main activity.  To "clock-in" merely touch the task you're starting.  To change tasks, touch another task - you will be "clocked-out" of the first and in to the second.  To stop working on any project, touch the one you're using.  When you're clocked-in to a task, the "checked" marker will be active to the right of the text.

Long-touching a task will allow the task to be renamed or retired from the list.  Adding a task is accomplished through a menu option.  Also in the menu are options for a daily and weekly report or summary, reviving a retired task and setting application preferences.

Application preferences currently include the time increment to report from the following list: 1, 2, 3, 4, 5, 6, 10, 12, 20, 30 and 60 minutes.  These are the even divisors of 60 and the complete list I thought would be reasonable.  Additional intervals can be added by adjusting the resources and assembling the package.  There's also a preference to automatically adjust the start/end times of tasks along the current increment preference.

I've implemented unit tests with the Positron test instrumentation suite and have reasonable coverage over the features and preferences.