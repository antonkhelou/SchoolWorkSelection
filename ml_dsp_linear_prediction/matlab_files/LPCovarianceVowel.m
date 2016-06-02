clear;
clf;
[vowel, Fs] = wavread('vowel_8_mono.wav');
vowel_x = filter(1,[1 1/2 1/3 1/4 1/5 1/6 1/7 1/8],vowel);

order = 15;

% Apply linear prediction using the covariance method
[a, g] = arcov(vowel_x, order);

estimated_vowel = filter([0 -a(2:end)], 1, vowel_x);
error_signal = vowel_x - estimated_vowel;
error_energy = sum(error_signal.^2);
[H, W] = freqz(sqrt(error_energy), a);

vowel_x_spectrum = abs(fft(vowel_x,1024));
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
plot(linspace(0,0.5,length(vowel_x_spectrum)/2),  20*log10(vowel_x_spectrum(1:length(vowel_x_spectrum)/2)), 'g');
legend('Input Vowel Spectrum')

pause;
clf;

% Plot the frequency response of the filter 
freqz(sqrt(error_energy), a);
pause;

% Display results
subplot(4,1,1);
plot(vowel_x,'g');
title('Linear Predictive Analysis, Autocorrelation Method');
hold on;
plot(estimated_vowel);
hold off;
legend('vowel Signal','Estimated vowel Signal');

subplot(4,1,2);
plot(error_signal);
legend('Error Signal');

subplot(4,1,3);
plot(linspace(0,0.5,length(error_spectrum)/2), error_spectrum(1:length(error_spectrum)/2));
legend('Error Signal Spectrum (1024 points)')

subplot(4,1,4);
plot(linspace(0,0.5,length(H)),  20*log10(abs(H)));
hold on;
plot(linspace(0,0.5,length(vowel_x_spectrum)/2),  20*log10(vowel_x_spectrum(1:length(vowel_x_spectrum)/2)), 'g');
legend('Model Frequency Response','vowel Spectrum')
hold off;

pause;
clf;

% test on impulse_train.wav file
[w, fs, nbits] = wavread('impulse_train.wav');
impulse_train_spectrum = abs(fft(w,1024));

subplot(2,1,1);
plot(linspace(0,0.5,length(impulse_train_spectrum)/2), 20*log10(impulse_train_spectrum(1:length(impulse_train_spectrum)/2)));
legend('Impulse Train Spectrum (1024 points)')

% test on impulse_train.wav file
w_filtered = filter(1,a,w);
filtered_spectrum = abs(fft(w_filtered,1024));

subplot(2,1,2);
plot(linspace(0,0.5,length(H)),  20*log10(abs(H)));
hold on;
plot(linspace(0,0.5,length(filtered_spectrum)/2), 20*log10(filtered_spectrum(1:length(filtered_spectrum)/2)),'g');
legend('Model Frequency Response','Filtered Impulse Train Spectrum')
hold off;

pause;
sound(w,fs);
pause;
sound(w_filtered,fs);
