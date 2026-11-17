# Tools

Scripts that are not part of the Kotlin build.

- Golden fixtures: a Python script exports small reference outputs from PyTorch once.
  The JSON files go to `src/test/resources/golden/` of the module. The tests compare the Kotlin output with them.
  Each script lists its exact Python and PyTorch versions at the top.

You need Python only to make new fixtures. You do not need it to run the tests.
