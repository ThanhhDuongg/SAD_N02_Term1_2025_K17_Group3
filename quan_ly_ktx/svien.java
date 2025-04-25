public class svien {
    private String maSV;
    private String hoTen;
    private String diaChi;
    private String sdt;
    private String email;
    private String maPhong;

    public svien(String maSV, String hoTen, String diaChi, String sdt, String email, String maPhong) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.maPhong = maPhong;
    }

    public String getMaSV() {
        return maSV;
    }
    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }
    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    public String getDiaChi() {
        return diaChi;
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    public String getSdt() {
        return sdt;
    }
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMaPhong() {
        return maPhong;
    }
    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }
    @Override
    public String toString() {
        return "svien{" +
                "maSV='" + maSV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", sdt='" + sdt + '\'' +
                ", email='" + email + '\'' +
                ", maPhong='" + maPhong + '\'' +
                '}';
    }
    public String toCSV() {
        return maSV + "," + hoTen + "," + diaChi + "," + sdt + "," + email + "," + maPhong;
    }
    public static svien fromCSV(String csv) {
        String[] parts = csv.split(",");
        return new svien(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
    }
    public static void main(String[] args) {
        svien sv = new svien("SV001", "Nguyen Van A", "Ha Noi", "0123456789", "         