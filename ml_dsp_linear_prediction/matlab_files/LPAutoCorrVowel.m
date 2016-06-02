clear;
clf;
[vowel, Fs] = wavread('vowel_8_mono.wav');
vowel_x = filter(1,[1 1/2 1/3 1/4 1/5 1/6 1/7 1/8],vowel);

w = hamming(length(vowel_x));
win_vowel = w.*vowel_x;

order = 15;

% Apply linear prediction using the autocorrelation method
[a, g] = lpc(win_vowel, order);

estimated_vowel = filter([0 -a(2:end)], 1, win_vowel);
error_signal = win_vowel - estimated_vowel;
error_energy = sum(error_signal.^2);
[H, W] = freqz(sqrt(error_energy), a);

win_vowel_spectrum = abs(fft(win_vowel,1024));
error_spectrum = abs(fft(error_signal,1024));

% Reconstruct the original using the residual (LPC)
reconstructed_signal = filter(1, a, error_signal);
recons_sig_spectrum = abs(fft(reconstructed_signal,1024));

% Plot the reconstructed signal and the original signal for comparison
subplot(2,1,1);
% only need half due to symmetry
plot(linspace(0,0.5,length(recons_sig_spectrum)/2),  20*log10(recons_sig_spectrum(1:length(recons_sig_spectrum)/2)));
legend('Reconstructed Signal Frequency Response')

subplot(2,1,2);
plot(linspace(0,0.5,length(win_vowel_spectrum)/2),  20*log10(win_vowel_spectrum(1:length(win_vowel_spectrum)/2)), 'g');
legend('Input Vowel Spectrum')

pause;
clf;

% Plot the frequency response of the filter 
freqz(sqrt(error_energy), a);
pause;

% Display results
subplot(4,1,1);
plot(win_vowel,'g');
title('Linear Predictive Analysis, Autocorrelation Method');
hold on;
plot(estimated_vowel);
hold off;
legend('Vowel Signal','Estimated Vowel Signal');

subplot(4,1,2);
plot(error_signal);
legend('Error Signal');

subplot(4,1,3);
plot(linspace(0,0.5,length(error_spectrum)/2), error_spectrum(1:length(error_spectrum)/2));
legend('Error Signal Spectrum (1024 points)')

subplot(4,1,4);
plot(linspace(0,0.5,length(H)),  20*log10(abs(H)));
hold on;
plot(linspace(0,0.5,length(win_vowel_spectrum)/2),  20*log10(win_vowel_spectrum(1:length(win_vowel_spectrum)/2)), 'g');
legend('Model Frequency Response','Vowel Spectrum')
hold off;

pause;
clf;

% Test on impulse_train.wav file
[w, fs, nbits] = wavread('impulse_train.wav');
impulse_train_spectrum = abs(fft(w,1024));
w_filtered = filter(1,a,w);
filtered_spectrum = abs(fft(w_filtered,1024));

% Plot the original spectrum and the filtered to compare
subplot(2,1,1);
plot(linspace(0,0.5,length(impulse_train_spectrum)/2), 20*log10(impulse_train_spectrum(1:length(impulse_train_spectrum)/2)));
legend('Impulse Train Spectrum (1024 points)')

subplot(2,1,2);
plot(linspace(0,0.5,length(H)),  20*log10(abs(H)));
hold on;
plot(linspace(0,0.5,length(filtered_spectrum)/2), 20*log10(filtered_spectrum(1:length(filtered_spectrum)/2)),'g');
legend('Model Frequency Response','Filtered Impulse Train Spectrum')
hold off;


% Play the impulse clips
pause;
sound(w,fs);
pause;
sound(w_filtered,fs);