package com.ifanferdi.aplikasitokobukuv2.Model;

import com.google.firebase.Timestamp;

public class ModelKeranjang {
    private String id;
    private String id_user;
    private String id_buku;
    private Long harga;
    private Long jumlah;
    private Long total;
    private Timestamp waktu;

    public ModelKeranjang(String id, String id_user, String id_buku, Long harga, Long jumlah, Long total, Timestamp waktu) {
        this.id = id;
        this.id_user = id_user;
        this.id_buku = id_buku;
        this.harga = harga;
        this.jumlah = jumlah;
        this.total = total;
        this.waktu = waktu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_buku() {
        return id_buku;
    }

    public void setId_buku(String id_buku) {
        this.id_buku = id_buku;
    }

    public Long getHarga() {
        return harga;
    }

    public void setHarga(Long harga) {
        this.harga = harga;
    }

    public Long getJumlah() {
        return jumlah;
    }

    public void setJumlah(Long jumlah) {
        this.jumlah = jumlah;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Timestamp getWaktu() {
        return waktu;
    }

    public void setWaktu(Timestamp waktu) {
        this.waktu = waktu;
    }
}
