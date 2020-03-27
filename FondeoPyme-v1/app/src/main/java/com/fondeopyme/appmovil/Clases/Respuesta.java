package com.fondeopyme.appmovil.Clases;

import com.google.gson.Gson;

public class Respuesta {

    private String jsonrpc;
    private Result result;
    private Errores error;
    private String id;
    private String cookie_id;
    private long expiration;

    public static Respuesta json(String data) {
        return new Gson().fromJson(data, Respuesta.class);
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Respuesta.Result getResult() {
        return result;
    }

    public void setResult(Respuesta.Result result) {
        result = result;
    }

    public Errores getError() {
        return error;
    }

    public void setError(Errores error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCookie_id() {
        return cookie_id;
    }

    public void setCookie_id(String cookie_id) {
        this.cookie_id = cookie_id;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public static class Result {
        private String session_id;
        private String uid;
        private boolean is_system;
        private boolean is_admin;
        private String db;
        private String server_version;
        private String name;
        private String username;
        private String partner_display_name;

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public boolean isIs_system() {
            return is_system;
        }

        public void setIs_system(boolean is_system) {
            this.is_system = is_system;
        }

        public boolean isIs_admin() {
            return is_admin;
        }

        public void setIs_admin(boolean is_admin) {
            this.is_admin = is_admin;
        }

        public String getDb() {
            return db;
        }

        public void setDb(String db) {
            this.db = db;
        }

        public String getServer_version() {
            return server_version;
        }

        public void setServer_version(String server_version) {
            this.server_version = server_version;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPartner_display_name() {
            return partner_display_name;
        }

        public void setPartner_display_name(String partner_display_name) {
            this.partner_display_name = partner_display_name;
        }
    }

    public static class Errores {
        private int code;
        private String message;
        private Data data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    public static class Data {
        private String name;
        private String message;
        private String[] arguments;
        private String exception_type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String[] getArguments() {
            return arguments;
        }

        public void setArguments(String[] arguments) {
            this.arguments = arguments;
        }

        public String getException_type() {
            return exception_type;
        }

        public void setException_type(String exception_type) {
            this.exception_type = exception_type;
        }
    }
}
