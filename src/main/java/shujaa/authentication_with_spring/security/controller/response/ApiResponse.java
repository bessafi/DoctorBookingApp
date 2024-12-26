package shujaa.authentication_with_spring.security.controller.response;

public class ApiResponse {
    private String mensaje;

    public ApiResponse() {
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public ApiResponse(String mensaje) {
        this.mensaje = mensaje;
    }
}
