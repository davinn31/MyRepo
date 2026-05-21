package com.example // Tetap pertahankan nama package asli dari file Anda

import com.lagradost.cloudstream3.*
import org.jsoup.Jsoup

class ExampleProvider : MainAPI() { 
    // 1. Ubah konfigurasi URL target dan Nama Provider Anda
    override var mainUrl = "https://movieboxonline.org" 
    override var name = "MovieBox Pribadi"
    override val supportedTypes = setOf(TvType.Movie)
    override var lang = "en"
    override val hasMainPage = true

    // 2. Tambahkan Fungsi getMainPage untuk mengambil data halaman depan MovieBox
    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        // app.get() bertugas melakukan HTTP GET Request ke https://movieboxonline.org
        val htmlMentah = app.get("$mainUrl/homepage").text
        
        // Jsoup bertugas membedah dokumen HTML mentah menjadi pohon struktur DOM
        val dokumen = Jsoup.parse(htmlMentah)
        val daftarFilm = ArrayList<SearchResponse>()

        // Menggunakan CSS Selector akurat "p.movie-card-title" yang Anda temukan saat Inspect Element
        dokumen.select("p.movie-card-title").forEach { elemen ->
            val judulFilm = elemen.text().trim()
            
            // Bungkus data teks ke dalam objek SearchResponse standar CloudStream
            daftarFilm.add(newMovieSearchResponse(judulFilm, "$mainUrl/homepage", TvType.Movie) {
                // Gunakan gambar ikon bawaan web sebagai poster penanda sementara
                this.posterUrl = "https://movieboxonline.org" 
            })
        }
        
        // Kembalikan hasilnya ke aplikasi Android TV Anda
        return newHomePageResponse(listOf(HomePageList("Trending MovieBox", daftarFilm)), false)
    }

    // Fungsi pencarian bawaan template, kita biarkan kosong terlebih dahulu agar tidak error
    override suspend fun search(query: String): List<SearchResponse> {
        return listOf()
    }
}
class CloudstreamPlugin : Plugin() {
    override fun load(context: Context) { // 'Context' wajib menggunakan huruf C kapital
        // Melakukan registrasi agar Cloudstream mengenali Provider Anda saat diunduh
        registerProvider(ExampleProvider())
    }
}
