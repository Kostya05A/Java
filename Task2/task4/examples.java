static int example1() {
  try {
    return 1;
  } finally {
    System.out.println("finally");
  }
}
// it works like:
// 1. count return 1;
// 2. runs finally;
// 3. method return 1;


static int example2() {
  try {
    return 1;
  } finally {
    return 2;
  }
}
// returns 2. return 1 will be lost


static int example3() {
  try {
    throw new RuntimeException("A");
  } finally {
    return 42;
  }
}
// throw exception will be lost, method will return 42
