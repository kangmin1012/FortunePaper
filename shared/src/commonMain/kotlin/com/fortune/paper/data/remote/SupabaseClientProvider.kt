package com.fortune.paper.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions

// SUPABASE_URL과 publishable key는 local.properties(Android) / Secrets.xcconfig(iOS)에서 주입
object SupabaseClientProvider {
    lateinit var client: SupabaseClient
        private set

    fun initialize(url: String, anonKey: String) {
        client = createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = anonKey
        ) {
            install(Functions)
        }
    }
}
