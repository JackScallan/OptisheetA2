package ie.wit.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

import ie.wit.R
import ie.wit.adapters.DonationAdapter
import ie.wit.adapters.DonationListener
import ie.wit.models.SheetModel
import kotlinx.android.synthetic.main.fragment_vault.view.*

class VaultAllFragment : VaultFragment(),
    DonationListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_vault, container, false)
        activity?.title = getString(R.string.menu_report_all)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))

        var query = FirebaseDatabase.getInstance()
            .reference.child("donations")

        var options = FirebaseRecyclerOptions.Builder<SheetModel>()
            .setQuery(query, SheetModel::class.java)
            .setLifecycleOwner(this)
            .build()

        root.recyclerView.adapter = DonationAdapter(options, this)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VaultAllFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
