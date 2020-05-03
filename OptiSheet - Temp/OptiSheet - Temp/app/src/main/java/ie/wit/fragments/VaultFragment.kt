package ie.wit.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import ie.wit.R
import ie.wit.adapters.DonationAdapter
import ie.wit.adapters.DonationListener
import ie.wit.main.OptiSheetApp
import ie.wit.models.SheetModel
import ie.wit.utils.*
import kotlinx.android.synthetic.main.fragment_vault.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

open class VaultFragment : Fragment(), AnkoLogger,
    DonationListener {

    lateinit var app: OptiSheetApp
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as OptiSheetApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_vault, container, false)
        activity?.title = getString(R.string.action_report)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))

        var query = FirebaseDatabase.getInstance()
            .reference
            .child("user-donations").child(app.currentUser.uid)

        var options = FirebaseRecyclerOptions.Builder<SheetModel>()
            .setQuery(query, SheetModel::class.java)
            .setLifecycleOwner(this)
            .build()

        root.recyclerView.adapter = DonationAdapter(options, this)

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteDonation((viewHolder.itemView.tag as SheetModel).uid)
                deleteUserDonation(app.currentUser!!.uid,
                    (viewHolder.itemView.tag as SheetModel).uid)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onDonationClick(viewHolder.itemView.tag as SheetModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VaultFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    fun deleteUserDonation(userId: String, uid: String?) {
        app.database.child("user-donations").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    fun deleteDonation(uid: String?) {
        app.database.child("donations").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    override fun onDonationClick(sheet: SheetModel) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, EditFragment.newInstance(sheet))
            .addToBackStack(null)
            .commit()
    }
}
