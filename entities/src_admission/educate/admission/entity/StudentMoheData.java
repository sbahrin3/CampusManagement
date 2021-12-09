package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity @Table(name="mohe_student_data")
public class StudentMoheData {
	
	@Id @Column(length=50)
	private String matric;
	@Column(length=50)
	private String nokp;
	@Column(length=100)
	private String nama_penuh;
	@Column(length=50)
	private String email;
	@Column(length=50)
	private String jantina;
	@Column(length=50)
	private String tarikh_lahir;
	@Column(length=50)
	private String warganegara;
	@Column(length=50)
	private String negara_asal;
	@Column(length=50)
	private String alamat_tetap1;
	@Column(length=50)
	private String alamat_tetap2;
	@Column(length=50) 
	private String alamat_tetap3;
	@Column(length=50)
	private String negeri_tetap;
	@Column(length=50)
	private String daerah_tetap;
	@Column(length=50)
	private String poskod_tetap;
	@Column(length=50)
	private String keturunan;
	@Column(length=50)
	private String peringkat_pengajian;
	@Column(length=50)
	private String bidang_pengajian;
	@Column(length=50)
	private String subbidang_pengajian;
	@Column(length=50)
	private String national_education_code;
	@Column(length=50)
	private String fakulti;
	@Column(length=50)
	private String ijazah;
	@Column(length=50)
	private String ijazah_singkatan;
	@Column(length=50)
	private String kod_kursus;
	@Column(length=50)
	private String program;
	@Column(length=50)
	private String pengkhususan;
	@Column(length=50)
	private String cgpa;
	@Column(length=50)
	private String gred;
	@Column(length=50)
	private String lokasi_pengajian;
	@Column(length=50)
	private String mod_pengajian;
	@Column(length=50)
	private String kaedah_pengajian;
	@Column(length=50)
	private String bulan_mula_pengajian;
	@Column(length=50)
	private String tahun_mula_pengajian;
	@Column(length=50)
	private String bulan_tamat_pengajian;
	@Column(length=50)
	private String tahun_tamat_pengajian;
	@Column(length=50)
	private String penaja;
	@Column(length=50)
	private String spm_bm;
	@Column(length=50)
	private String spm_bi;
	@Column(length=50)
	private String muet;
	@Column(length=50)
	private String cawangan;
	@Column(length=50)
	private String francais;
	@Column(length=50)
	private String nama_institusi_francais;
	@Column(length=50)
	private String kelayakan_masuk;
	@Column(length=50)
	private String kelayakan_lain;
	@Column(length=50)
	private String status_latihan_industri;
	@Column(length=50)
	private String status_oku;
	@Column(length=50)
	private String jenis_oku;
	@Transient
	private int totalFields = 47;
	private int filledFields = 0;
	
	public StudentMoheData() {
		
	}
	
	public String getMatric() {
		return matric;
	}
	public void setMatric(String matric) {
		this.matric = matric;
	}
	public String getNokp() {
		return nokp;
	}
	public void setNokp(String nokp) {
		this.nokp = nokp;
	}
	public String getNama_penuh() {
		return nama_penuh;
	}
	public void setNama_penuh(String nama_penuh) {
		this.nama_penuh = nama_penuh;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getJantina() {
		return jantina;
	}
	public void setJantina(String jantina) {
		this.jantina = jantina;
	}
	public String getTarikh_lahir() {
		return tarikh_lahir;
	}
	public void setTarikh_lahir(String tarikh_lahir) {
		this.tarikh_lahir = tarikh_lahir;
	}
	public String getWarganegara() {
		return warganegara;
	}
	public void setWarganegara(String warganegara) {
		this.warganegara = warganegara;
	}
	public String getNegara_asal() {
		return negara_asal;
	}
	public void setNegara_asal(String negara_asal) {
		this.negara_asal = negara_asal;
	}
	public String getAlamat_tetap1() {
		return alamat_tetap1;
	}
	public void setAlamat_tetap1(String alamat_tetap1) {
		this.alamat_tetap1 = alamat_tetap1;
	}
	public String getAlamat_tetap2() {
		return alamat_tetap2;
	}
	public void setAlamat_tetap2(String alamat_tetap2) {
		this.alamat_tetap2 = alamat_tetap2;
	}
	public String getAlamat_tetap3() {
		return alamat_tetap3;
	}
	public void setAlamat_tetap3(String alamat_tetap3) {
		this.alamat_tetap3 = alamat_tetap3;
	}
	public String getNegeri_tetap() {
		return negeri_tetap;
	}
	public void setNegeri_tetap(String negeri_tetap) {
		this.negeri_tetap = negeri_tetap;
	}
	public String getDaerah_tetap() {
		return daerah_tetap;
	}
	public void setDaerah_tetap(String daerah_tetap) {
		this.daerah_tetap = daerah_tetap;
	}
	public String getPoskod_tetap() {
		return poskod_tetap;
	}
	public void setPoskod_tetap(String poskod_tetap) {
		this.poskod_tetap = poskod_tetap;
	}
	public String getKeturunan() {
		return keturunan;
	}
	public void setKeturunan(String keturunan) {
		this.keturunan = keturunan;
	}
	public String getPeringkat_pengajian() {
		return peringkat_pengajian;
	}
	public void setPeringkat_pengajian(String peringkat_pengajian) {
		this.peringkat_pengajian = peringkat_pengajian;
	}
	public String getBidang_pengajian() {
		return bidang_pengajian;
	}
	public void setBidang_pengajian(String bidang_pengajian) {
		this.bidang_pengajian = bidang_pengajian;
	}
	public String getSubbidang_pengajian() {
		return subbidang_pengajian;
	}
	public void setSubbidang_pengajian(String subbidang_pengajian) {
		this.subbidang_pengajian = subbidang_pengajian;
	}
	public String getNational_education_code() {
		return national_education_code;
	}
	public void setNational_education_code(String national_education_code) {
		this.national_education_code = national_education_code;
	}
	public String getFakulti() {
		return fakulti;
	}
	public void setFakulti(String fakulti) {
		this.fakulti = fakulti;
	}
	public String getIjazah() {
		return ijazah;
	}
	public void setIjazah(String ijazah) {
		this.ijazah = ijazah;
	}
	public String getIjazah_singkatan() {
		return ijazah_singkatan;
	}
	public void setIjazah_singkatan(String ijazah_singkatan) {
		this.ijazah_singkatan = ijazah_singkatan;
	}
	public String getKod_kursus() {
		return kod_kursus;
	}
	public void setKod_kursus(String kod_kursus) {
		this.kod_kursus = kod_kursus;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getPengkhususan() {
		return pengkhususan;
	}
	public void setPengkhususan(String pengkhususan) {
		this.pengkhususan = pengkhususan;
	}
	public String getCgpa() {
		return cgpa;
	}
	public void setCgpa(String cgpa) {
		this.cgpa = cgpa;
	}
	public String getGred() {
		return gred;
	}
	public void setGred(String gred) {
		this.gred = gred;
	}
	public String getLokasi_pengajian() {
		return lokasi_pengajian;
	}
	public void setLokasi_pengajian(String lokasi_pengajian) {
		this.lokasi_pengajian = lokasi_pengajian;
	}
	public String getMod_pengajian() {
		return mod_pengajian;
	}
	public void setMod_pengajian(String mod_pengajian) {
		this.mod_pengajian = mod_pengajian;
	}
	public String getKaedah_pengajian() {
		return kaedah_pengajian;
	}
	public void setKaedah_pengajian(String kaedah_pengajian) {
		this.kaedah_pengajian = kaedah_pengajian;
	}
	public String getBulan_mula_pengajian() {
		return bulan_mula_pengajian;
	}
	public void setBulan_mula_pengajian(String bulan_mula_pengajian) {
		this.bulan_mula_pengajian = bulan_mula_pengajian;
	}
	public String getTahun_mula_pengajian() {
		return tahun_mula_pengajian;
	}
	public void setTahun_mula_pengajian(String tahun_mula_pengajian) {
		this.tahun_mula_pengajian = tahun_mula_pengajian;
	}
	public String getBulan_tamat_pengajian() {
		return bulan_tamat_pengajian;
	}
	public void setBulan_tamat_pengajian(String bulan_tamat_pengajian) {
		this.bulan_tamat_pengajian = bulan_tamat_pengajian;
	}
	public String getTahun_tamat_pengajian() {
		return tahun_tamat_pengajian;
	}
	public void setTahun_tamat_pengajian(String tahun_tamat_pengajian) {
		this.tahun_tamat_pengajian = tahun_tamat_pengajian;
	}
	public String getPenaja() {
		return penaja;
	}
	public void setPenaja(String penaja) {
		this.penaja = penaja;
	}
	public String getSpm_bm() {
		return spm_bm;
	}
	public void setSpm_bm(String spm_bm) {
		this.spm_bm = spm_bm;
	}
	public String getSpm_bi() {
		return spm_bi;
	}
	public void setSpm_bi(String spm_bi) {
		this.spm_bi = spm_bi;
	}
	public String getMuet() {
		return muet;
	}
	public void setMuet(String muet) {
		this.muet = muet;
	}
	public String getCawangan() {
		return cawangan;
	}
	public void setCawangan(String cawangan) {
		this.cawangan = cawangan;
	}
	public String getFrancais() {
		return francais;
	}
	public void setFrancais(String francais) {
		this.francais = francais;
	}
	public String getNama_institusi_francais() {
		return nama_institusi_francais;
	}
	public void setNama_institusi_francais(String nama_institusi_francais) {
		this.nama_institusi_francais = nama_institusi_francais;
	}
	public String getKelayakan_masuk() {
		return kelayakan_masuk;
	}
	public void setKelayakan_masuk(String kelayakan_masuk) {
		this.kelayakan_masuk = kelayakan_masuk;
	}
	public String getKelayakan_lain() {
		return kelayakan_lain;
	}
	public void setKelayakan_lain(String kelayakan_lain) {
		this.kelayakan_lain = kelayakan_lain;
	}
	public String getStatus_latihan_industri() {
		return status_latihan_industri;
	}
	public void setStatus_latihan_industri(String status_latihan_industri) {
		this.status_latihan_industri = status_latihan_industri;
	}
	public String getStatus_oku() {
		return status_oku;
	}
	public void setStatus_oku(String status_oku) {
		this.status_oku = status_oku;
	}
	public String getJenis_oku() {
		return jenis_oku;
	}
	public void setJenis_oku(String jenis_oku) {
		this.jenis_oku = jenis_oku;
	}

	public int getTotalFields() {
		return totalFields;
	}

	public void setTotalFields(int totalFields) {
		this.totalFields = totalFields;
	}

	public int getFilledFields() {
		return filledFields;
	}

	public void setFilledFields(int filledFields) {
		this.filledFields = filledFields;
	}
	
	
	
	
}
