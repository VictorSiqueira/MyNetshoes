package br.com.nurik.mynetshoes.pojo

import java.io.Serializable

/**
 * Model class which will br drive through whole app,
 * carrying data from Github's Gists as content, owner, etc
 */
class Gist(var url: String? =null,
           var forks_url:String? =null,
           var commits_url:String? =null,
           var id:String? =null,
           var node_id:String? =null,
           var git_pull_url:String? =null,
           var git_push_url:String? =null,
           var html_url:String? =null,
           var public:Boolean? =null,
           var created_at:String? =null,
           var updated_at:String? =null,
           var description:String? =null,
           var comments:Int? =null,
           var user:String? =null,
           var comments_url:String? =null,
           var truncated:Boolean? =null,
           var owner: Owner? =null,
           var files: Object ? =null,
           var favorite: Boolean = false): Serializable

