package com.mstoica.dogoapp.network

class NetworkResource<T> (
    val status: ResourceStatus,
    val data: T?,
    val message: String? = null
){
    enum class ResourceStatus {
        ERROR, LOADING, SUCCESS
    }

    companion object {
        fun <T> success(data: T?): NetworkResource<T> {
            return NetworkResource(ResourceStatus.SUCCESS, data)
        }

        fun <T> error(message: String? = null): NetworkResource<T> {
            return NetworkResource(ResourceStatus.ERROR, null, message)
        }

        fun <T> loading(): NetworkResource<T> {
            return NetworkResource(ResourceStatus.LOADING, null)
        }
    }
}