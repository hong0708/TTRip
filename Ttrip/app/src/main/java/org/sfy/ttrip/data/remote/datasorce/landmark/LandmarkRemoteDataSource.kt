package org.sfy.ttrip.data.remote.datasorce.landmark

interface LandmarkRemoteDataSource {

    suspend fun getLandmarks(): List<LandmarkResponse>
}