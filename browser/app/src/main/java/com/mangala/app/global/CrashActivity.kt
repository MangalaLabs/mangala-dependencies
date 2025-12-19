/**
 * Copyright (c) 2017 WillowTree, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mangala.app.global

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.schoolonair.wallet.browser.app.R


// Based on Hyperion-crash https://github.com/willowtreeapps/Hyperion-Android/blob/619ee2892d55dc5a308d77f9a284861759b76216/hyperion-crash/src/main/java/com/willowtreeapps/hyperion/crash/CrashActivity.java
class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crash)
    }

    override fun onStart() {
        super.onStart()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val fabCrash = findViewById<FloatingActionButton>(R.id.fab_crash)
        val headerText = findViewById<TextView>(R.id.header)
        val stackTraceText = findViewById<TextView>(R.id.stacktrace)
        val container = findViewById<View>(R.id.container)

        val report = intent.getSerializableExtra(EXTRA_CRASH_REPORT) as CrashReport

        headerText.text = report.headerText
        stackTraceText.text = report.stackTrace
        fab.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, report.headerText + "\n\n" + report.stackTrace)
            }, "Share crash report"))
        }
        fabCrash.setOnClickListener {
            report.rethrowThrowable()
        }
        container.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Crash report", report.headerText + "\n\n" + report.stackTrace)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val EXTRA_CRASH_REPORT = "EXTRA_CRASH_REPORT"

        fun start(context: Context, report: CrashReport) {
            val intent = Intent(context, CrashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(EXTRA_CRASH_REPORT, report)
            context.startActivity(intent)
        }
    }
}