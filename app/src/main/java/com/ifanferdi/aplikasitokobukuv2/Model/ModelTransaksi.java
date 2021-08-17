package com.ifanferdi.aplikasitokobukuv2.Model;

import com.google.firebase.Timestamp;
public class ModelTransaksi {

    private String no_transaksi;
    private String id_user;
    private Timestamp waktu;
    private Long total_bayar;
    private String status;
    private String bukti_pembayaran;
    private String resi;

    public ModelTransaksi(String no_transaksi, String id_user, Timestamp waktu, Long total_bayar, String status, String bukti_pembayaran, String resi) {
        this.no_transaksi = no_transaksi;
        this.id_user = id_user;
        this.waktu = waktu;
        this.total_bayar = total_bayar;
        this.status = status;
        this.bukti_pembayaran = bukti_pembayaran;
        this.resi = resi;
    }

    public String getNo_transaksi() {
        return no_transaksi;
    }

    public void setNo_transaksi(String no_transaksi) {
        this.no_transaksi = no_transaksi;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public Timestamp getWaktu() {
        return waktu;
    }

    public void setWaktu(Timestamp waktu) {
        this.waktu = waktu;
    }

    public Long getTotal_bayar() {
        return total_bayar;
    }

    public void setTotal_bayar(Long total_bayar) {
        this.total_bayar = total_bayar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBukti_pembayaran() {
        return bukti_pembayaran;
    }

    public void setBukti_pembayaran(String bukti_pembayaran) {
        this.bukti_pembayaran = bukti_pembayaran;
    }

    public String getResi() {
        return resi;
    }

    public void setResi(String resi) {
        this.resi = resi;
    }
}
