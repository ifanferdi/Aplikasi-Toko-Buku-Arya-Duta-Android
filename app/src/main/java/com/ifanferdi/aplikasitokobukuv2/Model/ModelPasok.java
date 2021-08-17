package com.ifanferdi.aplikasitokobukuv2.Model;


import com.google.firebase.Timestamp;

public class ModelPasok {
    String id, judul_buku, nama_distributor;
    Long jumlah;
    Timestamp tanggal;

    public ModelPasok(String id, String judul_buku, String nama_distributor, Long jumlah, Timestamp tanggal) {
        this.id = id;
        this.judul_buku = judul_buku;
        this.nama_distributor = nama_distributor;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul_buku() {
        return judul_buku;
    }

    public void setJudul_buku(String judul_buku) {
        this.judul_buku = judul_buku;
    }

    public String getNama_distributor() {
        return nama_distributor;
    }

    public void setNama_distributor(String nama_distributor) {
        this.nama_distributor = nama_distributor;
    }

    public Long getJumlah() {
        return jumlah;
    }

    public void setJumlah(Long jumlah) {
        this.jumlah = jumlah;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }
}
