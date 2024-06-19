package com.example.worknet.recommendationSystem;

import java.util.*;

import com.example.worknet.entities.Job;
import com.example.worknet.entities.Skill;
import com.example.worknet.entities.User;

public class RecommendationSystem {

    private double[][] P;
    private double[][] Q;
    private int[][] userJobMatrix;

    public void createInteractionMatrix(List<User> users, List<Job> jobs) {
        int numUsers = users.size();
        int numJobs = jobs.size();

        this.userJobMatrix = new int[numUsers][numJobs];

        // Create mappings from IDs to indices
        Map<Long, Integer> userIdToIndex = new HashMap<>();
        Map<Long, Integer> jobIdToIndex = new HashMap<>();

        // Populate the userIdToIndex map
        for (int i = 0; i < numUsers; i++) {
            userIdToIndex.put(users.get(i).getId(), i);
        }

        // Populate the jobIdToIndex map
        for (int i = 0; i < numJobs; i++) {
            jobIdToIndex.put(jobs.get(i).getId(), i);
        }

        // Populate user job interaction matrix
        for (User user : users) {
            for (Job job : jobs) {
                Long userId = user.getId();
                Long jobId = job.getId();
                int viewCount = user.countViewsForJob(job.getId());

                // Get the mapped indices
                int userIndex = userIdToIndex.get(userId);
                int jobIndex = jobIdToIndex.get(jobId);

                this.userJobMatrix[userIndex][jobIndex] = viewCount;
            }
        }
    }

    public void matrixFactorization(int numUsers, int numJobs, int numFactors) {
        this.P = new double[numUsers][numFactors];
        this.Q = new double[numJobs][numFactors];

        initializeMatrix(this.P);
        initializeMatrix(this.Q);

        double learningRate = 0.01;
        double lambda = 0.1;
        int numIterations = 100;

        for (int iter = 0; iter < numIterations; iter++) {
            for (int u = 0; u < numUsers - 1; u++) {
                for (int j = 0; j < numJobs; j++) {
                    if (this.userJobMatrix[u][j] > 0) {
                        double predictedRating = dotProduct(this.P[u], this.Q[j]);
                        double error = this.userJobMatrix[u][j] - predictedRating;
                        for (int k = 0; k < numFactors; k++) { // implemented gradient descent to improve results
                            this.P[u][k] += learningRate * (error * this.Q[j][k] - lambda * this.P[u][k]);
                            this.Q[j][k] += learningRate * (error * this.P[u][k] - lambda * this.Q[j][k]);
                        }
                    }
                }
            }
        }
    }

    public List<Job> getRecommendedJobs(User user, List<Job> jobs) {
        int numJobs = Q.length;

        // Get results for my user
        double[] userPredictions = new double[numJobs];
        for (int j = 0; j < numJobs; j++) {
            userPredictions[j] = dotProduct(this.P[this.P.length - 1], this.Q[j]); // get last element of array, which is wanted user, to get results back.
        }

        // Get top-N recommendations
        int N = 10; // Number of recommendations
        List<Integer> recommendedJobIndexes = getTopNRecommendations(userPredictions, N);
        List<Job> recommendedJobs = new ArrayList<>();
        for (int index: recommendedJobIndexes) {
            recommendedJobs.add(jobs.get(index));
        }

        return recommendedJobs;

    }

    private static List<Integer> getTopNRecommendations(double[] scores, int N) {
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> Double.compare(scores[b], scores[a]));
        for (int i = 0; i < scores.length; i++) {
            pq.offer(i);
        }

        List<Integer> recommendations = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (!pq.isEmpty()) {
                recommendations.add(pq.poll());
            }
        }
        return recommendations;
    }

    public HashSet<Job> recommendJobsBySkill(User user, List<Job> jobs) {
        List<Skill> userSkills = user.getSkills();
        HashSet<Job> recommendedJobs = new HashSet<Job>(); // hash set to remove duplicates.

        Set<String> userSkillNames = new HashSet<>();
        for (Skill userSkill : userSkills) {
            userSkillNames.add(userSkill.getName().trim().toLowerCase()); // case insensitive skill search.
        }

        for (Job job : jobs){
            List<Skill> individualJobSkills = job.getSkills();
            for (Skill skill : individualJobSkills){
                if (userSkillNames.contains(skill.getName().trim().toLowerCase())) {
                    recommendedJobs.add(job);
                }
            }
        }

        return recommendedJobs;
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
