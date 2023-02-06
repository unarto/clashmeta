package com.github.kr328.clash.design

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.github.kr328.clash.design.databinding.DesignSettingsCommonBinding
import com.github.kr328.clash.design.preference.*
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HelpDesign(
    context: Context,
    openLink: (Uri) -> Unit,
) : Design<Unit>(context) {
    private val binding = DesignSettingsCommonBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.surface = surface

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)
        var info=TextView(context)
        info.text=queryAppVersionName()
        val screen = preferenceScreen(context) {
            tips(R.string.tips_help)

            category(R.string.document)

            clickable(
                title = R.string.clash_wiki,
                summary = R.string.clash_wiki_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_wiki_url)))
                }
            }

            clickable(
                title = R.string.clash_meta_wiki,
                summary = R.string.clash_meta_wiki_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_meta_wiki_url)))
                }
            }

            category(R.string.sources)

            clickable(
                title = R.string.clash_meta_core,
                summary = R.string.clash_meta_core_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_meta_core_url)))
                }
            }


            clickable(
                title = R.string.clash_meta_for_android,
                summary = R.string.meta_github_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.meta_github_url)))
                }
            }

//            info
        }

        binding.content.addView(screen.root)
    }
    private  fun queryAppVersionName(): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName

    }
}
