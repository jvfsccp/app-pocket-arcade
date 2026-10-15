package com.pocketarcade.app;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pocketarcade.app.databinding.ItemSorteioBinding;

import java.util.List;

/**
 * O Adapter fornece as views dos itens; o ViewHolder as guarda para reutilização.
 */
public class ItemSorteioAdapter extends RecyclerView.Adapter<ItemSorteioAdapter.ItemViewHolder> {

    /** Avisa a Activity quando um item recebe um clique longo (exclusão). */
    public interface OnItemLongClickListener {
        void onItemLongClick(int posicao);
    }

    private final List<ItemSorteio> itens;
    private final OnItemLongClickListener listener;
    private ItemSorteio destacado;

    public ItemSorteioAdapter(List<ItemSorteio> itens, OnItemLongClickListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    /** Marca o item sorteado e atualiza apenas as duas linhas afetadas. */
    public void destacar(ItemSorteio novo) {
        ItemSorteio anterior = destacado;
        destacado = novo;
        int posicaoAnterior = anterior == null ? -1 : itens.indexOf(anterior);
        if (posicaoAnterior >= 0) {
            notifyItemChanged(posicaoAnterior);
        }
        int posicaoNova = novo == null ? -1 : itens.indexOf(novo);
        if (posicaoNova >= 0) {
            notifyItemChanged(posicaoNova);
        }
    }

    public ItemSorteio getDestacado() {
        return destacado;
    }

    public void limparDestaqueSe(ItemSorteio item) {
        if (destacado == item) {
            destacado = null;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSorteioBinding binding = ItemSorteioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemSorteio item = itens.get(position);
        holder.binding.tvPalavra.setText(item.getPalavra());
        holder.binding.tvCategoria.setText(
                holder.itemView.getContext().getString(R.string.categoria_do_item, item.getCategoria()));
        holder.binding.cardItem.setBackgroundResource(
                item == destacado ? R.drawable.bg_card_destaque : R.drawable.bg_card);

        holder.itemView.setOnLongClickListener(v -> {
            int posicao = holder.getBindingAdapterPosition();
            if (posicao != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(posicao);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ItemSorteioBinding binding;

        ItemViewHolder(ItemSorteioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
