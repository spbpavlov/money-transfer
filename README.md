# Test task for Revolut

Design comments:
- Balance is holding in cent in long and represented as String for perfomance puprose

Some assumptions:
- Account deleting assumses account deactivation. 
- To deactivate account balance must be empty
- Account creates always as active, if account was deactivated - it couldnt be activated again (only way to create new)
- While transfering API user must provide 2 amounts (for withdrow and for deposit) 
assuming all commistions and conversation between curruncies already was calculated properly
- Assume we could withdraw from account only if balance is enouth (no overdraft possible)