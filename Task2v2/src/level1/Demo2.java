public class Demo2 {
    public static void main(String[] args) {
        try { 
            invalidArg(-1); 
        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { 
            missingFile(); 
        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { 
            stateViolation(); 
        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { 
            networkFail(); 
        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { configError(); 

        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { divByZero(); 

        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { nullPointer(); 

        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { arrayBounds(); 

        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { dbConstraint(); 

        } catch (Exception e) { 
            System.out.println(e); 
        }
        try { businessRule(); 

        } catch (Exception e) { 
            System.out.println(e); 
        }
    }

    static void invalidArg(int val) { 
        if (val < 0) throw new IllegalArgumentException("Negative"); 
    }
    static void missingFile() throws java.io.FileNotFoundException { 
        throw new java.io.FileNotFoundException("Not found"); 
    }
    static void stateViolation() {
        throw new IllegalStateException("Bad state"); 
    }
    static void networkFail() throws java.io.IOException { 
        throw new java.io.IOException("Network down"); 
    }
    static void configError() { 
        throw new IllegalStateException("Missing config"); 
    }
    static void divByZero() { 
        int x = 1 / 0; 
    }
    static void nullPointer() { 
        String s = null; s.length(); 
    }
    static void arrayBounds() { 
        int[] a = new int[1]; 
        int x = a[5]; 
    }
    static void dbConstraint() throws java.sql.SQLException { 
        throw new java.sql.SQLException("Unique constraint"); 
    }
    static void businessRule() throws BusinessException { 
        throw new BusinessException("Rule broken"); 
    }
}
class BusinessException extends Exception {
    BusinessException(String msg) { super(msg); }
}