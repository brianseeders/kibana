#!/bin/groovy

library 'kibana-pipeline-library'
kibanaLibrary.load()

// workers.base(name: 'ci-worker', size: 'xxl', ramDisk: true, bootstrapped: false) {
//   def PARALLEL_COUNT = 50
//   def ITERATIONS_PER = 100

//   def tasks = [:]
//   for (def i = 0; i < PARALLEL_COUNT; i++) {
//     tasks["task-${i}"] = {
//       for (def j = 0; j < ITERATIONS_PER; j++) {
//         runbld('./echo.sh', "Execution ${i}-${j}")
//       }
//     }
//   }

//   parallel(tasks)
// }

workers.base(name: 'ci-worker', size: 's', ramDisk: false, bootstrapped: false) {
  runbld('./loop.sh', 'Loop')
}
