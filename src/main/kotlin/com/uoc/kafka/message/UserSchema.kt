package com.uoc.kafka.message


data class UserSchema (
    val type: String,
    val fields: List<Field>
){
    companion object {
        fun instance(): UserSchema {
            val registerTime = Field("registertime", "int64", false)
            val userId = Field("userid", "string", false)
            val regionId = Field("regionid", "string", false)
            val gender = Field("gender", "string", false)
            return UserSchema("struct", listOf(registerTime, userId, regionId, gender))
        }
    }
}
