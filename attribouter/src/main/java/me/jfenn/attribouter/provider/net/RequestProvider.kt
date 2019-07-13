package me.jfenn.attribouter.provider.net

import io.reactivex.Observable
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.UserData

interface RequestProvider {

    val id: String

    fun getUser(id: String): Observable<UserData>

    fun getRepository(id: String): Observable<RepoData>

    fun getContributors(id: String): Observable<Array<UserData>>

    fun getLicense(id: String): Observable<LicenseData>

    fun destroy()

}