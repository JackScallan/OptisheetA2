package ie.wit.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.wit.R
import ie.wit.main.OptiSheetApp
import ie.wit.models.SheetModel
import ie.wit.utils.createLoader
import ie.wit.utils.hideLoader
import ie.wit.utils.showLoader
import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: OptiSheetApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editSheet: SheetModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as OptiSheetApp

        arguments?.let {
            editSheet = it.getParcelable("editdonation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        root.editAmount.setText(editSheet!!.amount.toString())
        root.editPaymenttype.setText(editSheet!!.paymenttype)
        root.editMessage.setText(editSheet!!.message)
        root.editUpvotes.setText(editSheet!!.upvotes.toString())

        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating Donation on Server...")
            updateDonationData()
            updateDonation(editSheet!!.uid, editSheet!!)
            updateUserDonation(app.currentUser!!.uid,
                               editSheet!!.uid, editSheet!!)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(sheet: SheetModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editdonation",sheet)
                }
            }
    }

    fun updateDonationData() {
        editSheet!!.amount = root.editAmount.text.toString().toInt()
        editSheet!!.message = root.editMessage.text.toString()
        editSheet!!.upvotes = root.editUpvotes.text.toString().toInt()
    }

    fun updateUserDonation(userId: String, uid: String?, sheet: SheetModel) {
        app.database.child("user-donations").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(sheet)
                        activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFrame, VaultFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    fun updateDonation(uid: String?, sheet: SheetModel) {
        app.database.child("donations").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(sheet)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }
}
