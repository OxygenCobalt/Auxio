package org.oxycblt.auxio.music.picker

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately

/**
 * A picker [ViewBindingDialogFragment] intended for when [Genre] playback is ambiguous.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenrePlaybackPickerDialog : ViewBindingDialogFragment<DialogMusicPickerBinding>(), ClickableListListener {
    private val pickerModel: PickerViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    // Information about what Song to show choices for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel a Song.
    private val args: GenrePlaybackPickerDialogArgs by navArgs()
    // Okay to leak this since the Listener will not be called until after initialization.
    private val genreAdapter = GenreChoiceAdapter(@Suppress("LeakingThis") this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_genres).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        binding.pickerRecycler.adapter = genreAdapter

        pickerModel.setItemUid(args.itemUid)
        collectImmediately(pickerModel.genreChoices) { genres ->
            if (genres.isNotEmpty()) {
                // Make sure the genre choices align with any changes in the music library.
                genreAdapter.submitList(genres)
            } else {
                // Not showing any choices, navigate up.
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    override fun onClick(item: Item) {
        // User made a choice, play the given song from that genre.
        check(item is Genre) { "Unexpected datatype: ${item::class.simpleName}" }
        val song = pickerModel.currentItem.value
        check(song is Song) { "Unexpected datatype: ${item::class.simpleName}" }
        playbackModel.playFromGenre(song, item)
    }
}