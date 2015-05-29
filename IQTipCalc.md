#summary Simple but effective tip calculator
#labels Featured

# Introduction #

This is a simple tip calculator that has a few features I've missed from the
palm application I used to use.

# Details #

There are several fields that can be filled in: bill total, tip amount,
tip percentage, coupon value, tax value and tax percentage.  When one of
these are filled in, and another field is selected, the application will
attempt to update the other fields based on what you've changed.  Here's
an example:

If you've set your default tax rate to 8%, and your default tip percentage
to 15%, and you enter $10 as the bill total, the application will calculate
the remainder of the fields as so:

bill total: $10
coupon: $0
tip amount: $1.50
grand total: $11.50
tax amount: $0.74

For the moment, the application assumes that tax is included in the bill
total and that you want to include the tax in the tip.  This will be changed
to a preference in the near future.

If you change the tip amount to $2.00, the tip percentage is recalculated
to be 20% and the grand total is updated to $12.00.