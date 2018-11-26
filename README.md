# Test task for Revolut

Design comments:
- Balance is holding in cents in "long"-type (for the performance purposes) and is represented at DTO as "String"-type

Some assumptions:
- Account deleting assumes account deactivation. 
- Balance must be empty To enable account deactivation
- Account is always created as active, if account is deactivated  it can’t be activated again
- User must provide 2 amounts to transfer api(for withdraw and for deposit) 
assuming that all commissions and conversion between currencies are already calculated properly
- It’s Assumed that we are able to withdraw money from account only if the balance is enough (no overdraft is possible)