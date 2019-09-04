package br.com.nurik.mynetshoes.pojo

import java.io.Serializable

/**
 * Model Class of a Github's Gist Owner, which carry data from this user
 */
class Owner(var login: String? =null,
            var id: Int? =null,
            var node_id: String? =null,
            var avatar_url: String? =null,
            var gravatar_id: String? =null,
            var url: String? =null,
            var html_url: String? =null,
            var followers_url: String? =null,
            var following_url: String? =null,
            var gists_url: String? =null,
            var starred_url: String? =null,
            var subscriptions_url: String? =null,
            var organizations_url: String? =null,
            var repos_url: String? =null,
            var events_url: String? =null,
            var received_events_url: String? =null,
            var type: String? =null,
            var site_admin: Boolean? =null):Serializable {

}
