package level5.Demo15;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class CourseHub {
    public String processImport(String data) {
        try (Reader reader = new StringReader(data)) {
            validateData(data);
            return importInternal(reader);
        } catch (DomainException e) {
            return "ERROR:" + e.getCode();
        } catch (InfrastructureException e) {
            return "ERROR:INFRA";
        } catch (Exception e) {
            throw new RuntimeException("Programming error", e);
        }
    }

    private void validateData(String data) {
        if (data.contains("bad")) throw new DomainException("DOMAIN_01");
    }

    private String importInternal(Reader reader) throws InfrastructureException {
        try {
            if (reader.read() == -1) throw new InfrastructureException("Empty", new IOException("EOF"));
            return "OK";
        } catch (IOException e) {
            throw new InfrastructureException("IO fail", e);
        }
    }
}

class DomainException extends RuntimeException {
    private final String code;
    DomainException(String code) { super(code); this.code = code; }
    String getCode() { return code; }
}

class InfrastructureException extends Exception {
    InfrastructureException(String msg, Throwable cause) { super(msg, cause); }
}