package com.ifanferdi.aplikasitokobukuv2.Model;

public class ModelBuku {
    private String id, kode, judul, noisbn, penulis, penerbit, tahun;
    private Long stok;
    private String kategori;
    private Long harga, diskon;
    private String keterangan;
    private String image;

    public ModelBuku(){}

    public ModelBuku(String id, String kode, String judul, String noisbn, String penulis, String penerbit, String tahun, Long stok, String kategori, Long harga, Long diskon, String keterangan, String image) {
        this.id = id;
        this.kode = kode;
        this.judul = judul;
        this.noisbn = noisbn;
        this.penulis = penulis;
        this.penerbit = penerbit;
        this.tahun = tahun;
        this.stok = stok;
        this.kategori = kategori;
        this.harga = harga;
        this.diskon = diskon;
        this.keterangan = keterangan;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getNoisbn() {
        return noisbn;
    }

    public void setNoisbn(String noisbn) {
        this.noisbn = noisbn;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public String getPenerbit() {
        return penerbit;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public Long getStok() {
        return stok;
    }

    public void setStok(Long stok) {
        this.stok = stok;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public Long getHarga() {
        return harga;
    }

    public void setHarga(Long harga) {
        this.harga = harga;
    }

    public Long getDiskon() {
        return diskon;
    }

    public void setDiskon(Long diskon) {
        this.diskon = diskon;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
