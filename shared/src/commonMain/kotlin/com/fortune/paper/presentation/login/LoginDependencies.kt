package com.fortune.paper.presentation.login

import com.fortune.paper.auth.KakaoAuth
import com.fortune.paper.core.toad.ActionDependencies
import com.fortune.paper.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class LoginDependencies(
    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    val userRepository: UserRepository,
    val kakaoAuth: KakaoAuth
) : ActionDependencies()
