package com.ifanferdi.aplikasitokobukuv2.Model;

public class ModelDetailTransaksi {
    String id_detail_transaksi;
    String no_transaksi;
    String id_buku;
    Long harga;
    Long jumlah;
    Long total;

    public ModelDetailTransaksi(String id_detail_transaksi, String no_transaksi, String id_buku, Long harga, Long jumlah, Long total) {
        this.id_detail_transaksi = id_detail_transaksi;
        this.no_transaksi = no_transaksi;
        this.id_buku = id_buku;
        this.harga = harga;
        this.jumlah = jumlah;
        this.total = total;
    }

    public String getId_detail_transaksi() {
        return id_detail_transaksi;
    }

    public void setId_detail_transaksi(String id_detail_transaksi) {
        this.id_detail_transaksi = id_detail_transaksi;
    }

    public String getNo_transaksi() {
        return no_transaksi;
    }

    public void setNo_transaksi(String no_transaksi) {
        this.no_transaksi = no_transaksi;
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
}
