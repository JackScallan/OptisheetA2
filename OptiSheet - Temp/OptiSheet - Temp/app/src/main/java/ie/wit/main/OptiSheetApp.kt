package ie.wit.main

import android.app.Application
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class OptiSheetApp : Application(), AnkoLogger {

    lateinit var currentUser: FirebaseUser
    lateinit var database: DatabaseReference
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var storage: StorageReference
    lateinit var userImage: Uri

    override fun onCreate() {
        super.onCreate()
        info("Donation App started")
    }
}

