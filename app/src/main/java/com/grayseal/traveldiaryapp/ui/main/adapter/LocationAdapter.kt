import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grayseal.traveldiaryapp.R

class LocationAdapter(
    private val locationList: List<String>,
    private val onLocationClickedListener: OnLocationClickedListener
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location_layout, parent, false)
        return ViewHolder(view, onLocationClickedListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationName = locationList[position]
        holder.locationNameTextView.text = locationName
    }

    override fun getItemCount(): Int {
        return locationList.size
    }


    inner class ViewHolder(itemView: View, onLocationClickedListener: OnLocationClickedListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val containerView: View = itemView.findViewById(R.id.item_listing_container)
        val locationNameTextView: TextView = itemView.findViewById(R.id.location_name_text_view)
        private val onLocationClickedListener: OnLocationClickedListener

        init {
            this.onLocationClickedListener = onLocationClickedListener
            containerView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            this.onLocationClickedListener.onLocationClicked(adapterPosition)
        }
    }

    interface OnLocationClickedListener {
        fun onLocationClicked(position: Int)
    }
}
