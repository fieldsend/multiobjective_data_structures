function Y = glasmacher_analytical_sampler_with_uniform(m, N, n_d, c, d)

% Implementation of the analystical sampler detailed in 
%
% T. Glasmachers. 2017. A Fast Incremental BSP Tree Archive for 
% Non-dominated Points. In EMO 2017 (LNCS), H. Trautmann et al. (Ed.), 
% Vol. 10173. Springer,252?266
%
% (in section 6.1)
%
% however uses Uniform rather than Guassian internally -- use  
% glasmacher_analytical_sampler_with_guassian for standard version
%
% INPUTS
%
% m = number of objectives
% N = number of total samples
% N_d = number of dominated samples
% c = weighting factor c, c < 1 means more dominated samples toward the end
%     of the sequence, c > 1 means more dominated samples toward the start
%     of the sequence
% d = multiplier when a dominated sample desired
%
% OUTPUTS
%
% Y = an N by m matrix of objective samples vectors, ordered from t =1...
%     t=N
%
% (c) Jonathan Fieldsend, University of Exeter, 2020

Y = zeros(N,m);

sigma = eye(m,m) - 1/m;

Y(1,:) = rand(1,m)*sigma; % first draw is always non-dominated!  

for k=2:N
    if rand() < (c * n_d /(N-k))
        % dominated draw
        Y(k,:) = rand(1,m)*sigma + d*N/k;
        n_d = n_d-1;
    else
        % non-dominated draw
        Y(k,:) = rand(1,m)*sigma;
    end
end

