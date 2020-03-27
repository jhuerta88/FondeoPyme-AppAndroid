package com.fondeopyme.appmovil.Clases;

public class Autenticacion {

    private Params params;

    public Autenticacion() {
        params = new Params();
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public class Params {
        private String db;
        private String login;
        private String password;


        public String getDb() {
            return db;
        }

        public void setDb(String db) {
            this.db = db;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


}
