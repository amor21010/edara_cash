package net.edara.edaracash.models

import net.edara.domain.models.ProfileResponse


sealed class UserState {
    object TokenExpired : UserState()
    object UnAuthorized : UserState()
    object NotAllowed : UserState()
    class Success(val user: ProfileResponse.Data,val token: String) : UserState()
    class Failure(val error: String) : UserState()
    object Init : UserState()
}