clear;
clf;
noise = randn(1000,1);  % Normalized white Gaussian noise
noise_x = filter(1,[1 1/2 1/3 1/4 1/5 1/6 1/7 1/8],noise);

w = hamming(length(noise_x));
win_noise = w.*noise_x;

order = 10;

% Apply linear prediction using the autocorrelation method
[a, g] = lpc(win_noise, order);

estimated_noise = filter([0 -a(2:end)], 1, win_noise);
error_signal = win_noise - estimated_noise;
error_energy = sum(error_signal.^2);
[H, W] = freqz(sqrt(error_energy), a);

win_noise_spectrum = abs(fft(win_noise,1024));
error_spectrum = abs(fft(error_signal,1024));

% Plot the frequency response of the filter 
freqz(sqrt(error_energy), a);
pause;

% Display results
subplot(4,1,1);
plot(win_noise,'g');
title('Linear Predictive Analysis, Autocorrelation Method');
hold on;
plot(estimated_noise);
hold off;
legend('Noise Signal','Estimated Noise Signal');

subplot(4,1,2);
plot(error_signal);
legend('Error Signal');

subplot(4,1,3);
%only need half due to symmetry
plot(linspace(0,0.5,length(error_spectrum)/2), error_spectrum(1:length(error_spectrum)/2));
legend('Error Signal Spectrum (1024 points)')

subplot(4,1,4);
plot(linspace(0,0.5,length(H)),  20*log10(abs(H)));
hold on;
plot(linspace(0,0.5,length(win_noise_spectrum)/2),  20*log10(win_noise_spectrum(1:length(win_noise_spectrum)/2)), 'g');
ylim([0 70])
legend('Model Frequency Response','Noise Spectrum')
hold off;
