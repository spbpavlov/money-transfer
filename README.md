# Test task for Revolut

Design comments:
- To run tests please use "runTest.sh"
- To run app please use "runApp.sh"
- When the app is running API specification is available at localhost:7000/spec
- Balance is holding in cents in "long"-type (for the performance purposes) and is represented at DTO as "String"-type
- Lombok is used for getters/setters generation

Some assumptions:
- Account deleting assumes account deactivation. 
- Balance must be empty to enable account deactivation
- Account is always created as active, if account is deactivated  it can’t be activated again
- User must provide 2 amounts to transfer api (for withdraw and for deposit) assuming that all 
commissions and conversion between currencies are already calculated properly
- It’s assumed that customer is able to withdraw money from account only if the balance is enough (no overdraft is possible)