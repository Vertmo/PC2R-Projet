open Protocol

let usage = "usage: " ^ Sys.argv.(0) ^ " -a <server_adress> -p <server_port>"

(* Constants *)
let refresh_tickrate = 60

let addr= ref "localhost"
let port = ref 1234

let speclist = [
  "-a", Arg.Set_string addr, "Server address";
  "-p", Arg.Set_int port, "Server port";
]

let main addr port username =
  let s = Connection.start addr port in

  (* terminator must be executed to gracefully close the client *)
  let terminator () = (Connection.send s (Exit username); exit 0) in

  (* Redefine signal so that ctrl-c in the terminal exits the game gracefully *)
  ignore (Sys.signal Sys.sigint (Sys.Signal_handle (fun _ -> terminator ())));

  (* Initializing *)
  Connection.send s (Connect username);
  Game.state.player.username <- username;

  (* Launching graphics and interaction threads *)
  ignore (Thread.create Interface.graph_thread (refresh_tickrate, terminator));
  ignore (Thread.create Interface.input_thread terminator);

  while true do
    Game.move (); (* We leave that in in partie B to 'predict' the position of the player *)
    try (* Incoming signals could interrupt this and break everything *)
      Thread.delay (1./.(float_of_int refresh_tickrate));
    with Unix.Unix_error _ -> ()
  done

let _ =
  Arg.parse speclist (fun u -> main !addr !port u) usage
