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

        fun <T> error(message: String? = null, data: T? = null): NetworkResource<T> {
            return NetworkResource(ResourceStatus.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): NetworkResource<T> {
            return NetworkResource(ResourceStatus.LOADING, data)
        }
    }
}