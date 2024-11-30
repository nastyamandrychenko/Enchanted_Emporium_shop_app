package hu.bme.aut.qrvhfq.EnchantedEmporium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Product
import hu.bme.aut.qrvhfq.myapplication.databinding.SpecialItemBinding

class ProductsAdapter :RecyclerView.Adapter<ProductsAdapter.TrendingNowProductsViewHolder>() {
    inner class TrendingNowProductsViewHolder(private val binding: SpecialItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(productImage)
                productTitle.text = product.name
                productPrice.text = "$${product.price.toInt()}"
            }
        }
    }

        private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }

        val differ = AsyncListDiffer(this, diffCallback)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TrendingNowProductsViewHolder {
            return TrendingNowProductsViewHolder(
                SpecialItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: TrendingNowProductsViewHolder, position: Int) {
            val product = differ.currentList[position]
            holder.bind(product)

            holder.itemView.setOnClickListener {
                onClick?.invoke(product)
            }
        }

        override fun getItemCount(): Int {
            return differ.currentList.size
        }

        var onClick: ((Product) -> Unit)? = null
    fun submitList(list: List<Product>) {
        differ.submitList(list)
    }

}

