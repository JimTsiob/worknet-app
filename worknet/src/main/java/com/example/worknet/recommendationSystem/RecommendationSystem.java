package com.example.worknet.recommendationSystem;

import java.util.List;
import java.util.Random;

import com.example.worknet.entities.Job;
import com.example.worknet.entities.User;

public class RecommendationSystem {

    private double[][] P;
    private double[][] Q;
    private int[][] userJobMatrix;

    public void createInteractionMatrix(List<User> users, List<Job> jobs) {

        int numUsers = users.size();
        int numJobs = jobs.size();

        this.userJobMatrix = new int[numUsers][numJobs];

        // Populate user job interaction matrix.
        for (User user: users){
            for (Job job: jobs){
                Long userId = user.getId();
                Long jobId = job.getId();
                int viewCount = user.countViewsForJob(job.getId());
                // Convert Long to int
                int intUserId = userId.intValue();
                int intJobId = jobId.intValue();

                this.userJobMatrix[intUserId][intJobId] = viewCount;
            }
        }
    }

    private void matrixFactorization(int numUsers, int numJobs, int numFactors) {
        initializeMatrix(this.P);
        initializeMatrix(this.Q);

        double learningRate = 0.01;
        double lambda = 0.1;
        int numIterations = 10;

        for (int iter = 0; iter < numIterations; iter++) {
            for (int u = 0; u < numUsers; u++) {
                for (int j = 0; j < numJobs; j++) {
                    if (userJobMatrix[u][j] > 0) {
                        double predictedRating = dotProduct(P[u], Q[j]);
                        double error = userJobMatrix[u][j] - predictedRating;
                        for (int k = 0; k < numFactors; k++) {
                            this.P[u][k] += learningRate * (error * Q[j][k] - lambda * P[u][k]);
                            this.Q[j][k] += learningRate * (error * P[u][k] - lambda * Q[j][k]);
                        }
                    }
                }
            }
        }
    }



    private static double dotProduct(double[] vec1, double[] vec2) {
        double result = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            result += vec1[i] * vec2[i];
        }
        return result;
    }

    private void initializeMatrix(double[][] matrix) {
        Random random = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }
    }

}
