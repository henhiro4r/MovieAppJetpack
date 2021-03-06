package byc.avt.movieappjetpack.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import byc.avt.movieappjetpack.R;
import byc.avt.movieappjetpack.adapter.MovieAdapter;
import byc.avt.movieappjetpack.view.MainActivity;
import byc.avt.movieappjetpack.viewmodel.MovieViewModel;

public class MovieFragment extends Fragment {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_movie)
    RecyclerView rvMovie;
    @BindView(R.id.listError)
    TextView tvError;

    private MovieViewModel viewModel;
    private MovieAdapter movieAdapter;

    public MovieFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        showLoading(true);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setIcon(R.drawable.ic_outline_movie);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(" " + getString(R.string.title_movie));
        rvMovie.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(getActivity());

        viewModel = ViewModelProviders.of(requireActivity()).get(MovieViewModel.class);

        viewModel.refresh();

        refreshLayout.setOnRefreshListener(() -> {
            showLoading(true);
            viewModel.forceRefresh();
            refreshLayout.setRefreshing(false);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.listMovies.observe(requireActivity(), movies -> {
            if (movies != null) {
                movieAdapter.setMovies(movies);
                movieAdapter.notifyDataSetChanged();
                rvMovie.setVisibility(View.VISIBLE);
                rvMovie.setAdapter(movieAdapter);
            }
        });

        viewModel.movieError.observe(requireActivity(), error -> {
            if (error != null) {
                tvError.setVisibility(error ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.loading.observe(requireActivity(), loading -> {
            if (loading != null) {
                showLoading(loading);
            }
        });
    }

    private void showLoading(Boolean state) {
        if (state) {
            rvMovie.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
