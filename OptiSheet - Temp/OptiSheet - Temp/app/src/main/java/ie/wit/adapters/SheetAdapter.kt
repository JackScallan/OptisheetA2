package ie.wit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import ie.wit.R
import ie.wit.fragments.VaultAllFragment
import ie.wit.models.SheetModel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_sheet.view.*
import kotlinx.android.synthetic.main.fragment_sheet.view.*

interface DonationListener {
    fun onDonationClick(sheet: SheetModel)
}

class DonationAdapter(options: FirebaseRecyclerOptions<SheetModel>,
                      private val listener: DonationListener?)
    : FirebaseRecyclerAdapter<SheetModel,
        DonationAdapter.DonationViewHolder>(options) {

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(sheet: SheetModel, listener: DonationListener) {
            with(sheet) {
                itemView.tag = sheet
                itemView.paymentamount.text = sheet.amount.toString()
                itemView.paymentmethod.text = sheet.paymenttype

                if(listener is VaultAllFragment)
                    ; // Do Nothing, Don't Allow 'Clickable' Rows
                else
                    itemView.setOnClickListener { listener.onDonationClick(sheet) }

                if(sheet.isfavourite) itemView.imagefavourite.setImageResource(android.R.drawable.star_big_on)

                if(!sheet.profilepic.isEmpty()) {
                    Picasso.get().load(sheet.profilepic.toUri())
                        //.resize(180, 180)
                        .transform(CropCircleTransformation())
                        .into(itemView.imageIcon)
                }
                else
                    itemView.imageIcon.setImageResource(R.mipmap.ic_launcher_homer_round)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {

        return DonationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_sheet, parent, false))
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int, model: SheetModel) {
        holder.bind(model,listener!!)
    }

    override fun onDataChanged() {
        // Called each time there is a new data snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
    }
}