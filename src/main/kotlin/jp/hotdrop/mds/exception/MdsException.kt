package jp.hotdrop.mds.exception

class MdsException constructor(
        val status: Int,
        override val message: String,
        override val cause: Throwable? = null
): RuntimeException(message, cause)