clear;
clf;
noise = randn(1000,1);  % Normalized white Gaussian noise
noise_x = filter(1,[1 1/2 1/3 1/4 1/5 1/6 1/7 1/8],noise);

order = 10;

% Apply linear prediction using the covariance method
[a, g] = arcov(noise_x, order);

estimated_noise = filter([0 -a(2:end)], 1, noise_x);
error_signal = noise_x - estimated_noise;
error_energy = sum(error_signal.^2);
[H, W] = freqz(sqrt(error_energy), a);

noise_x_spectrum = abs(fft(noise_x,1024));
error_spectrum = abs(fft(error_signal,1024));

% Plot the frequency response of the filter 
freqz(sqrt(error_energy), a);
pause;

% Display results
subplot(4,1,1);
plot(noise_x,'g');
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
plot(linspace(0,0.5,length(noise_x_spectrum)/2),  20*log10(noise_x_spectrum(1:length(noise_x_spectrum)/2)), 'g');
ylim([0 70])
legend('Model Frequency Response','Noise Spectrum')
hold off;
