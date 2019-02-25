(*******************************************************************************)
(*                            Protocol Commands                                *)
(*******************************************************************************)

(** Coordinates *)
type coord = float * float

let coord_of_string s =
  let strings = String.split_on_char 'Y' s in
  let xs = (String.sub (List.nth strings 0) 1 ((String.length (List.nth strings 0))-1))
  and ys = List.nth strings 1 in
  (float_of_string xs, float_of_string ys)

(** Parses set of coordinates for several players *)
let coords_of_string s =
  let parts = String.split_on_char '|' s in
  List.map (fun s -> let l = String.split_on_char ':' s in
             (List.nth l 0, coord_of_string (List.nth l 1))) parts

(** Commands from Client to Server *)
type clientcmd =
  (* Part A *)
  | Connect of string
  | Exit of string
  | NewPos of coord

(** Translate a client command to a string, to send it to the server *)
let string_of_clientcmd = function
  | Connect u -> Printf.sprintf "CONNECT/%s/" u
  | Exit u -> Printf.sprintf "EXIT/%s/" u
  | NewPos (x, y) -> Printf.sprintf "NEWPOS/X%fY%f/" x y

(** Phase: attente ou jeu *)
type phase = Attente | Jeu

let phase_of_string = function
  | "attente" -> Attente | "jeu" -> Jeu | _ -> failwith "Unknown phase"

(** Liste de scores *)
type scores = (string * int) list

let scores_of_string s =
  let scores = String.split_on_char '|' s in
  List.map (fun s -> let l = String.split_on_char ':' s in
             (List.nth l 0, int_of_string (List.nth l 1))) scores

(** Commands from Server to Client *)
type servercmd =
  (* Part A *)
  | Welcome of phase * scores * coord
  | Denied
  | NewPlayer of string
  | PlayerLeft of string
  | Session of (string * coord) list * coord
  | Winner of scores
  | Tick of (string * coord) list
  | NewObj of coord * scores

(** Parses a string to a server command *)
let servercmd_of_string s =
  let parts = String.split_on_char '/' s in
  match (List.hd parts) with
  | "WELCOME" -> Welcome (phase_of_string (List.nth parts 1),
                          scores_of_string (List.nth parts 2),
                          coord_of_string (List.nth parts 3))
  | "DENIED" -> Denied
  | "NEWPLAYER" -> NewPlayer (List.nth parts 1)
  | "PLAYERLEFT" -> PlayerLeft (List.nth parts 1)
  | "SESSION" -> Session (coords_of_string (List.nth parts 1),
                          coord_of_string (List.nth parts 2))
  | "WINNER" -> Winner (scores_of_string (List.nth parts 1))
  | "TICK" -> Tick (coords_of_string (List.nth parts 1))
  | "NEWOBJ" -> NewObj ((coord_of_string (List.nth parts 1)),
                        (scores_of_string (List.nth parts 2)))
  | _ -> failwith "Unknown command"