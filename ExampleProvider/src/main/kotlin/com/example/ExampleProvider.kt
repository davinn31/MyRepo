package com.example

import android.content.Context
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import org.jsoup.Jsoup

class ExampleProvider : MainAPI() {
    override var mainUrl = "https://movieboxonline.org"
    override var name = "MovieBox Pribadi"
    override val supportedTypes = setOf(TvType.Movie)
    override var lang = "en"
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        // Ambil langsung dari root URL karena /homepage tidak eksis
        val htmlMentah = app.get(mainUrl).text
        val dokumen = Jsoup.parse(htmlMentah)
        val daftarFilm = ArrayList<SearchResponse>()

        // Menggunakan selector alternatif yang umum pada template movie-box
        dokumen.select(".movie-post, .flw-item, .post-item").forEach { elemen ->
            val judulFilm = elemen.select(".movie-title, h2, h3").text().trim()
            val linkFilm = fixUrl(elemen.select("a").attr("href"))
            val poster = fixUrl(elemen.select("img").attr("src"))

            if (judulFilm.isNotEmpty() && linkFilm.isNotEmpty()) {
                daftarFilm.add(newMovieSearchResponse(judulFilm, linkFilm, TvType.Movie) {
                    this.posterUrl = poster
                })
            }
        }

        return newHomePageResponse(listOf(HomePageList("Trending MovieBox", daftarFilm)), false)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }
}

@CloudstreamPlugin
class ExampleProviderPlugin : Plugin() {
    override fun load(context: Context) {
        // Hapus 'plugin.', panggil langsung fungsi registrasinya
        registerProvider(ExampleProvider())
    }
}
