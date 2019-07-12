package me.jfenn.attribouter.provider.data

import io.reactivex.Observable
import me.jfenn.attribouter.data.github.ContributorsData
import me.jfenn.attribouter.data.github.LicenseData
import me.jfenn.attribouter.data.github.RepositoryData
import me.jfenn.attribouter.data.github.UserData

interface RequestProvider {

    val id: String

    fun getUser(id: String): Observable<UserData>

    fun getRepository(id: String): Observable<RepositoryData>

    fun getContributors(id: String): Observable<ContributorsData>

    fun getLicense(id: String): Observable<LicenseData>

    fun destroy()

}