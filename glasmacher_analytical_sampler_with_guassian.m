function [Y, meanIndex ] = glasmacher_analytical_sampler_with_guassian(m, N, n_d, c, d)

% Implementation of the analystical sampler detailed in 
%
% T. Glasmachers. 2017. A Fast Incremental BSP Tree Archive for 
% Non-dominated Points. In EMO 2017 (LNCS), H. Trautmann et al. (Ed.), 
% Vol. 10173. Springer,252?266
%
% (in section 6.1)
%
% INPUTS
%
% m = number of objectives
% N = number of total samples
% N_d = number of dominated samples
% c = weighting factor c, c < 1 means more dominated samples toward the end
%     of the sequence, c > 1 means more dominated samples toward the start
%     of the sequence
% d = multiplier in Guassian mean when a dominated sample desired
%
% OUTPUTS
%
% Y = an N by m matrix of objective samples vectors, ordered from t =1...
%     t=N
%
% (c) Jonathan Fieldsend, University of Exeter, 2020

Y = zeros(N,m);
orig_n_d = N-n_d;

sigma = eye(m,m) - 1/m;

Y(1,:) = randn(1,m)*sigma; % first draw is always non-dominated!  
meanIndex = 1;
for k=2:N
    if rand() < (c * n_d /(N-k))
        % dominated draw
        Y(k,:) = randn(1,m)*sigma + d*N/k;
        n_d = n_d-1;
    else
        % non-dominated draw
        Y(k,:) = randn(1,m)*sigma;
        meanIndex = meanIndex + k;
    end
end

meanIndex = meanIndex/orig_n_d;

