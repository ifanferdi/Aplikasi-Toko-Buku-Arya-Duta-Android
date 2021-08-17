package com.ifanferdi.aplikasitokobukuv2.Model;

public class ModelDistributor {
    String id, nama_distributor, notelp, alamat;

    public ModelDistributor(String id, String nama_distributor, String notelp, String alamat) {
        this.id = id;
        this.nama_distributor = nama_distributor;
        this.notelp = notelp;
        this.alamat = alamat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_distributor() {
        return nama_distributor;
    }

    public void setNama_distributor(String nama_distributor) {
        this.nama_distributor = nama_distributor;
    }

    public String getNotelp() {
        return notelp;
    }

    public void setNotelp(String notelp) {
        this.notelp = notelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

}
