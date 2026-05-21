package com.example

import android.content.Context
import com.lagradost.cloudstream3.*
import org.jsoup.Jsoup

class ExampleProvider : MainAPI() {
    override var mainUrl = "https://movieboxonline.org"
    override var name = "MovieBox Pribadi"
    override val supportedTypes = setOf(TvType.Movie)
    override var lang = "en"
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val htmlMentah = app.get(mainUrl).text
        val dokumen = Jsoup.parse(htmlMentah)
        val daftarFilm = ArrayList<SearchResponse>()

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

// FORMAT MANIFEST RESMI: Menembak langsung ke path com.lagradost tanpa perantara import yang rusak
class ExampleProviderPlugin : com.lagradost.cloudstream3.plugins.Plugin() {
    override fun load(context: Context) {
        registerProvider(ExampleProvider())
    }
}
